package com.sundaysoul.repository;

import com.sundaysoul.model.StaticPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaticPageRepository extends JpaRepository<StaticPage, String> {
}
