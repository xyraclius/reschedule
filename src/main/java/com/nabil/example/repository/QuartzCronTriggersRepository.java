package com.nabil.example.repository;

import com.nabil.example.model.QuartzCronTriggersModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuartzCronTriggersRepository extends JpaRepository<QuartzCronTriggersModel, Long> {
    QuartzCronTriggersModel findBySchedName(String SchedName);
}
