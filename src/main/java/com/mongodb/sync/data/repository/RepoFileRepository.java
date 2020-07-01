package com.mongodb.sync.data.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mongodb.sync.data.entity.RepoFile;

/**
 * Description: 获取文件信息持久化操作
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/5/28.1       linzc    2020/5/28           Create
 * </pre>
 * @date 2020/5/28
 */
@Repository
public interface RepoFileRepository extends JpaRepository<RepoFile, String> {

	List<RepoFile> findAllByCreateTimeGreaterThanAndCreateTimeLessThanEqualOrderByCreateTime(Timestamp startTime,
			Timestamp endTime);

	RepoFile findByPhysicalFileId(String id);
}
