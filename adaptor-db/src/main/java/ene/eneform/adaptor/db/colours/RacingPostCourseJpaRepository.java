package ene.eneform.adaptor.db.colours;

import ene.eneform.domain.colours.RacingPostCourse;
import ene.eneform.port.out.colours.RacingPostCourseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface RacingPostCourseJpaRepository extends RacingPostCourseRepository, JpaRepository<RacingPostCourse, String> {
}
