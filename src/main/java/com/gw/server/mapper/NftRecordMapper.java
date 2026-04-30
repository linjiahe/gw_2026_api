package com.gw.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gw.server.entity.NftRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NftRecordMapper extends BaseMapper<NftRecord> {
    /**
     * 原子操作增加重试次数
     * 使用 IFNULL 是为了防止旧数据的 retry_count 为 NULL 导致相加后还是 NULL
     */
    @Update("UPDATE nft_record SET retry_count = IFNULL(retry_count, 0) + 1, updated_at = NOW() WHERE id = #{id}")
    int incrementRetry(@Param("id") Long id);


}
