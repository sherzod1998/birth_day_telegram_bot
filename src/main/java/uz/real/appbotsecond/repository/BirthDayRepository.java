package uz.real.appbotsecond.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.real.appbotsecond.model.BirthDay;
import uz.real.appbotsecond.model.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BirthDayRepository extends JpaRepository<BirthDay, Long> {

    List<BirthDay> findAllByUser(User user);

    List<BirthDay> findAllByDayAndMonth(int day, int month);

}
