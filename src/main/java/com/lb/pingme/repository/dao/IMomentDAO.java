package com.lb.pingme.repository.dao;

import com.lb.pingme.repository.entity.MomentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IMomentDAO extends JpaSpecificationExecutor<MomentEntity>,
        JpaRepository<MomentEntity, Long> {


    MomentEntity findAllById(Long id);

    Long countAllByStatus(String status);

    Long countAllByAuthorAndStatus(String author, String status);

    @Query(value = "update web_chat_moment c set c.click_count = c.click_count + 1 where c.id = ?1 ", nativeQuery = true)
    void updateCommunityClickCount(Long id);

    @Query(value = "select c.id from web_chat_moment c where c.is_recommend = 1 and c.status = 'PUBLISHED' order by c.recommend_date desc limit ?1", nativeQuery = true)
    List<Long> findRecommendCommunityIdList(Integer size);

    @Query(value = "select c.id from web_chat_moment c where c.is_top = 1 and c.status = 'PUBLISHED' and top_date > ?2 order by c.top_date desc limit ?1", nativeQuery = true)
    List<Long> findTopCommunityIdList(Integer size, Date startDate);

    @Query(value = "select c.id from web_chat_moment c where c.is_tec = 1 and c.status = 'PUBLISHED' order by c.tec_date desc limit ?1", nativeQuery = true)
    List<Long> findTecCommunityIdList(Integer size);

    @Query(value = "select c.id from MomentEntity c where c.status = ?1 order by c.id desc")
    List<Long> findAllByStatus(String status);

    @Query(value = "select c from MomentEntity c where c.status = ?1")
    List<MomentEntity> findAllEntityByStatus(String status);

    List<MomentEntity> findAllByPubDateAfter(Date date);

    @Query(value = "select c.id from web_chat_moment c where c.status = ?1 and c.pub_date > ?2 order by c.click_count desc limit 200", nativeQuery = true)
    List<Long> findAllByStatusOrderByClickCount(String status, Date pubDate);

    @Query(value = "select c.id from web_chat_moment c where c.type_id = ?1 and c.status = ?2 and c.pub_date > ?3 order by c.click_count desc limit 200", nativeQuery = true)
    List<Long> findAllByTypeIdAndStatusOrderByClickCount(Long typeId, String status, Date pubDate);

    @Query(value = "select c.id from MomentEntity c where c.author = ?1 and c.status = ?2 order by c.id desc")
    List<Long> findAllByAuthorAndStatus(String author, String status);

    @Query(value = "select count(*) from web_chat_moment t where t.status = ?1 and t.create_date = now()", nativeQuery = true)
    Long todayCountByStatus(String status);

    @Query(value = "select c from MomentEntity c where (c.createDate between ?1 and ?2)")
    List<MomentEntity> findAllByUpdateDateBetween(Date start, Date end);

    @Query(value = "select distinct(author) from web_chat_moment order by id desc limit ?1" , nativeQuery = true)
    List<String> findAuthorLastLimit(Integer size);

    @Query(value = "select c.* from web_chat_moment c where c.author = ?1 and status = 'PUBLISHED' "
            + "and c.pub_date < ?2 order by c.id desc limit ?3", nativeQuery = true)
    List<MomentEntity> loadUserPubCommunityList(String authorId, Date pubDate, int size);

    @Query(value = "select c.id from MomentEntity c where c.content like ?1 and c.status = 'PUBLISHED'")
    List<Long> searchAllByKeywords(String keyword, Pageable pageable);
}
