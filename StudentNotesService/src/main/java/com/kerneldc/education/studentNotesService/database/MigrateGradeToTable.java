package com.kerneldc.education.studentNotesService.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

public class MigrateGradeToTable implements CustomTaskChange {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	
	private static final String SELECT_STUDENTS_IN_2017_2018_SQL =
			"select distinct s.id,s.grade "
			+ "from student s join student_note sn on s.id=sn.student_id join note n on sn.note_id = n.id "
			+ "where n.timestamp>='2017-09-01'";
	private static final String SELECT_STUDENTS_IN_2016_2017_SQL =
			"select s.id,s.grade from student s "
			+ "minus "
			+ "select distinct s.id,s.grade "
			+ "from student s join student_note sn on s.id=sn.student_id join note n on sn.note_id = n.id "
			+ "where n.timestamp>='2017-09-01'";
	private static final String INSERT_STUDENT_SCHOOL_YEAR_SQL =
			"insert into student_school_year (student_id, school_year_id) values (?, ?)";
	private static final String INSERT_GRADE_SQL =
			"insert into grade (student_id , school_year_id , grade , version ) values (?, ?, ?, 0)";
	private static final String INSERT_STUDENT_GRADE_SQL =
			"insert into student_grade (student_id, grade_id) values (?, ?)";

	@Override
	public void execute(Database database) throws CustomChangeException {
		JdbcConnection jdbcConnection = (JdbcConnection) database.getConnection();
		try {
			PreparedStatement insertStudentSchoolYearStatement = jdbcConnection.prepareStatement(INSERT_STUDENT_SCHOOL_YEAR_SQL);
			PreparedStatement insertGradeStatement = jdbcConnection.prepareStatement(INSERT_GRADE_SQL,
			        Statement.RETURN_GENERATED_KEYS);
			PreparedStatement insertStudentGradeStatement = jdbcConnection.prepareStatement(INSERT_STUDENT_GRADE_SQL);
			Long schoolYearId2017_2018 = getSchoolYearId(jdbcConnection, "2017-2018");
			Long schoolYearId2016_2017 = getSchoolYearId(jdbcConnection, "2016-2017");
			migrateStudents(jdbcConnection, SELECT_STUDENTS_IN_2017_2018_SQL, schoolYearId2017_2018,
					insertStudentSchoolYearStatement, insertGradeStatement, insertStudentGradeStatement);
			migrateStudents(jdbcConnection, SELECT_STUDENTS_IN_2016_2017_SQL, schoolYearId2016_2017,
					insertStudentSchoolYearStatement, insertGradeStatement, insertStudentGradeStatement);
		} catch (DatabaseException | SQLException e) {
			throw new CustomChangeException("Student migration failed", e);
			
		}
	}

	private void migrateStudents(JdbcConnection jdbcConnection, String selectSql, Long schoolYearId,
			PreparedStatement insertStudentSchoolYearStatement, PreparedStatement insertGradeStatement,
			PreparedStatement insertStudentGradeStatement) throws SQLException {
		Statement statement = null;
		try {
			statement = jdbcConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(selectSql);
			while (resultSet.next()) {
				Long studentId = resultSet.getLong(1);
				String grade = resultSet.getString(2);
				LOGGER.debug("studentId: [{}], grade: [{}]", studentId, grade);
				insertStudentSchoolYearRow(insertStudentSchoolYearStatement, studentId, schoolYearId);
				Long gradeId = insertGradeRow(insertGradeStatement, studentId, schoolYearId, grade);
				LOGGER.debug("gradeId: [{}]", gradeId);
				insertStudentGradeRow(insertStudentGradeStatement, studentId, gradeId);
			}
		} catch (SQLException | DatabaseException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}
	
	private void insertStudentGradeRow(PreparedStatement insertStudentGradeStatement, Long studentId, Long gradeId) throws SQLException {
		insertStudentGradeStatement.setLong(1, studentId);
		insertStudentGradeStatement.setLong(2, gradeId);
		int rowCount = insertStudentGradeStatement.executeUpdate();
		if (rowCount == 0) {
			throw new SQLException(String.format("Unable to insert student_grade row with studentId: [%d] gradeId: [%d]", studentId,
					gradeId));
		}
	}
	
	private Long insertGradeRow(PreparedStatement insertGradeStatement, Long studentId,
			Long schoolYearId, String grade) throws SQLException {
		Long gradeId = null;
		insertGradeStatement.setLong(1, studentId);
		insertGradeStatement.setLong(2, schoolYearId);
		insertGradeStatement.setString(3, grade);
		int rowCount = insertGradeStatement.executeUpdate();
		if (rowCount == 0) {
			throw new SQLException(String.format("Unable to insert grade row with studentId: [%d] schoolYearId: [%d], grade: [%s]", studentId,
					schoolYearId, grade));
		}
		ResultSet generatedKeys = insertGradeStatement.getGeneratedKeys();
		if (generatedKeys.next()) {
			gradeId = generatedKeys.getLong(1);
		} else {
			throw new SQLException("Unable to obtain generated key for grade table");
		}
		return gradeId;
	}
	
	private void insertStudentSchoolYearRow(PreparedStatement insertStudentSchoolYearStatement, Long studentId, Long schoolYearId) throws SQLException {
		insertStudentSchoolYearStatement.setLong(1, studentId);
		insertStudentSchoolYearStatement.setLong(2, schoolYearId);
		int rowCount = insertStudentSchoolYearStatement.executeUpdate();
		if (rowCount == 0) {
			throw new SQLException(String.format("Unable to insert student_school_year row with studentId: [%d] schoolYearId: [%d]", studentId,
					schoolYearId));
		}
	}
	
	private Long getSchoolYearId(JdbcConnection jdbcConnection, String schoolYear) throws SQLException {
		Long schoolYearId = null;
		Statement statement = null;
		try {
			statement = jdbcConnection.createStatement();
			ResultSet resultSet = statement.executeQuery("select id from school_year where school_year='"+schoolYear+"'");
			if (resultSet.next()) {
				schoolYearId = resultSet.getLong(1);
			} else {
				throw new SQLException("School year ["+schoolYear+"] not found.");
			}
		} catch (SQLException | DatabaseException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
		return schoolYearId;
	}

	@Override
	public String getConfirmationMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUp() throws SetupException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
		// TODO Auto-generated method stub

	}

	@Override
	public ValidationErrors validate(Database database) {
		// TODO Auto-generated method stub
		return null;
	}

}
