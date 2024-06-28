package com.felipegc.booking.repositories;

import com.felipegc.booking.models.BlockModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface BlockRepository extends JpaRepository<BlockModel, UUID>, JpaSpecificationExecutor<BlockModel> {

    @Modifying
    @Query(value="delete from tb_block where property_id = :propertyId and block_id = :blockId", nativeQuery = true)
    void deleteBlockByPropertyIdAndBlockId(UUID propertyId, UUID blockId);
}
