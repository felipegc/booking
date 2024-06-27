package com.felipegc.booking.repositories;

import com.felipegc.booking.models.BlockModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BlockRepository extends JpaRepository<BlockModel, UUID>, JpaSpecificationExecutor<BlockModel> {
}
