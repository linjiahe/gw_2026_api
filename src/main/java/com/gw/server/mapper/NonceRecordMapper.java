package com.gw.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gw.server.entity.NonceRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NonceRecordMapper extends BaseMapper<NonceRecord> {
}
