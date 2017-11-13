package com.kerneldc.education.studentNotesService.mochito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

public class T1 {

	@SuppressWarnings("unchecked")
	@Test
	public void testOne() {
		// mock creation
		List<String> mockedList = mock(List.class);

		// using mock object - it does not throw any "unexpected interaction" exception
		mockedList.add("one");
		mockedList.clear();

		// selective, explicit, highly readable verification
		verify(mockedList).add("one");
		verify(mockedList).clear();	}

	@Test
	public void test1()  {
	        //  create mock
	        MyClass test = mock(MyClass.class);

	        // define return value for method getUniqueId()
	        when(test.getUniqueId()).thenReturn(43);

	        // use mock in test....
	        assertEquals(test.getUniqueId(), 43);
	        
	}
	@Test
	public void test2()  {
        MyClass test = new MyClass();

        assertEquals(test.getUniqueId(), 0);
	        
	}
}
