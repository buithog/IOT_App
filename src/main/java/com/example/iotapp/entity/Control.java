package com.example.iotapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Control {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date date;
    private boolean statusFan;
    private boolean statusLight;

    public Control( int statusFan, int statusLight) {
        this.date = new Date();
        if (statusFan >= 1){
            this.statusFan = true;
        }else {
            this.statusFan = false;
        }
        if (statusLight >= 1){
            this.statusLight = true;
        }else {
            this.statusLight = false;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.format(this.date);
    }
}
