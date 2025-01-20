package ene.eneform.adaptor.db.colours;

import ene.eneform.domain.colours.SportingLifeCourse;
import ene.eneform.port.out.colours.SportingLifeCourseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface SportingLifeCourseJpaRepository extends SportingLifeCourseRepository,  JpaRepository<SportingLifeCourse, String> {
}
