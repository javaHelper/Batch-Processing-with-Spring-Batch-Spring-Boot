package com.example.writer;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.model.StudentResponse;

@Component
public class FirstItemWriter implements ItemWriter<StudentResponse> {

    @Override
    public void write(Chunk<? extends StudentResponse> items) {
        System.out.println("Inside Item Writer");
        items.getItems().forEach(System.out::println);
    }
}
