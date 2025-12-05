package com.example.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FirstItemReader implements ItemReader<Integer> {

	List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

	int i = 0;

	@Override
	public Integer read() {
		System.out.println("Inside Item reader");

		Integer item;

		if (i < list.size()) {
			item = list.get(i);
			i++;
			return item;
		}
		i = 0;
		// There are no more records to read!
		return null;
	}

}
