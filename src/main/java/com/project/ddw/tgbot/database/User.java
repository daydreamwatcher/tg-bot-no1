package com.project.ddw.tgbot.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tg_data")
public class User {

    @Id
    private long id; // BigInt
    private String name; // Text
    private int msg_numb; // Integer
}
