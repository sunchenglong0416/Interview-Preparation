package com.sun.interview.models;

import lombok.Data;

@Data
public class Doctor {

    private String doctorId;

    private Integer nums;

    private Integer score;

    public Doctor() {}

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Doctor(String doctorId, Integer nums, Integer score) {
        this.doctorId = doctorId;
        this.nums = nums;
        this.score = score;
    }
}
