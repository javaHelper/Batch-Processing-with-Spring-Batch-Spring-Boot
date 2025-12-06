package com.example.listener;

import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import com.example.model.StudentCsv;

@Component
public class SkipListener {

    @OnSkipInRead
    public void onSkipInRead(Throwable t) {
        System.out.println(t.getMessage());
        if (t instanceof FlatFileParseException) {
            System.out.println("## Bad Record : " + ((FlatFileParseException) t).getInput());
        }
    }

    @OnSkipInProcess
    public void onSkipInProcess(StudentCsv studentCsv, Throwable t) {
        System.out.println("## Bad Record : " + studentCsv);
    }
}
