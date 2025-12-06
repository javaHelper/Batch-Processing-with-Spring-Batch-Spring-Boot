package com.example.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.model.StudentCsv;
import com.example.model.StudentJson;

@Component
public class FirstItemProcessor implements ItemProcessor<StudentCsv, StudentJson> {

    @Override
    public StudentJson process(StudentCsv item) throws Exception {
        System.out.println("Inside Item Processor");
        StudentJson studentJson = new StudentJson();

        if (item.getId() == 6) {
            throw new NullPointerException();
        }

        studentJson.setId(item.getId());
        studentJson.setFirstName(item.getFirstName());
        studentJson.setLastName(item.getLastName());
        studentJson.setEmail(item.getEmail());

        return studentJson;
    }
}
