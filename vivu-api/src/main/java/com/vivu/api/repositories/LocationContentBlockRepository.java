package com.vivu.api.repositories;

import com.vivu.api.entities.LocationContentBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationContentBlockRepository extends JpaRepository<LocationContentBlock, Integer> {
    // Nếu cần lấy các block riêng lẻ không qua Location entity:
    List<LocationContentBlock> findByLocationIdOrderByOrderIndexAsc(Integer locationId);

    // Xóa tất cả block của một location (dùng khi cập nhật, trước khi thêm block mới)
    void deleteByLocationId(Integer locationId);
}