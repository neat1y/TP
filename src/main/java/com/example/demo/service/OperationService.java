package com.example.demo.service;

import com.example.demo.models.Operations;
import com.example.demo.repositories.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperationService {
    private final OperationRepository operationRepository;
    @Autowired
    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    public void save(Operations operations) {
        operationRepository.save(operations);
    }

    public List<Operations> all() {
        return operationRepository.findAll();
    }

    public Optional<Operations> findByLast() {
        return operationRepository.findTopByOrderByIdDesc();
    }
}
