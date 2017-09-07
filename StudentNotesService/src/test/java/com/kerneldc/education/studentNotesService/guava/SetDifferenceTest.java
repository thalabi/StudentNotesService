package com.kerneldc.education.studentNotesService.guava;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import jersey.repackaged.com.google.common.collect.Sets;

public class SetDifferenceTest {

	private static final Long ONE = Long.valueOf(1l);
	private static final Long TWO = Long.valueOf(2l);
	private static final Long THREE = Long.valueOf(3l);
	private static final Long FOUR = Long.valueOf(4l);
	private static final Long FIVE = Long.valueOf(5l);
	@Test
	public void test() {
		Set<Long> oldIds = new HashSet<>();
		oldIds.addAll(Arrays.asList(ONE, TWO, THREE));
		Set<Long> newIds = new HashSet<>();
		newIds.addAll(Arrays.asList(ONE, FOUR, FIVE));
		//Sets.SetView<Long> addedIds = Sets.difference(newIds, oldIds);
		Set<Long> addedIds = Sets.difference(newIds, oldIds);
		assertThat(addedIds, hasSize(2));
		assertThat(addedIds, contains(FOUR, FIVE));
		Set<Long> removedIds = Sets.difference(oldIds, newIds);
		assertThat(removedIds, hasSize(2));
		assertThat(removedIds, contains(TWO, THREE));
	}

}
