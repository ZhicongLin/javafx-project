package com.mongodb.sync.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mongodb.sync.data.entity.RepoFileSyncResult;

/**
 * Description: 错误文件操作信息持久化
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
public interface RepoFileSyncResultRepository extends JpaRepository<RepoFileSyncResult, String> {

	Integer countByResultAndRepoFileUuid(Integer result, String repoFileUuid);
}
