package com.datn.demo.Repositories;

import com.datn.demo.DTO.TicketDetailsDTO;
import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Entities.InvoiceEntity;
import com.datn.demo.Entities.RoomEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Integer> {

	@Query("SELECT DISTINCT i FROM InvoiceEntity i " + "JOIN FETCH i.account a " + "JOIN FETCH i.showtime s "
			+ "JOIN FETCH i.invoiceDetails d " + "JOIN FETCH d.product")
	List<InvoiceEntity> findAllWithDetails(); // Truy vấn để lấy tất cả hóa đơn kèm thông tin liên quan

	@Query("SELECT new com.datn.demo.DTO.TicketDetailsDTO("
			+ "s.seatName, m.movieName, st.showDate, st.startTime, st.endTime, r.roomName, "
			+ "c.cinemaName, c.address, c.email, c.phoneNumber) " + "FROM TicketEntity t " + "JOIN t.seat s "
			+ "JOIN t.showtime st " + "JOIN st.movie m " + "JOIN st.room r " + "JOIN t.invoice i "
			+ "JOIN st.cinemaInformation c " + "WHERE i.invoiceId = :invoiceId")
	List<TicketDetailsDTO> findSeatMovieShowtimeDetailsByInvoiceId(@Param("invoiceId") Integer invoiceId);

	List<InvoiceEntity> findByAccount_AccountId(Integer accountId); // Đã sửa đổi

	List<InvoiceEntity> findByShowtime_ShowtimeId(int showtimeId);

	@Query("SELECT COUNT(i) > 0 FROM InvoiceEntity i " + "JOIN i.showtime s " + "JOIN s.movie m "
			+ "WHERE i.account.accountId = :accountId " + "AND m.movieId = :movieId")
	boolean existsInvoiceForMovie(@Param("accountId") Integer accountId, @Param("movieId") Integer movieId);
	
	@Query("SELECT i FROM InvoiceEntity i " +
		       "JOIN i.showtime s " +
		       "WHERE (s.originalShowDate = :originalShowDate or :originalShowDate is null) " +
		       "AND (CAST(s.originalStartTime AS time) = CAST(:originalStartTime AS time) or :originalStartTime is null) " +
		       "AND (CAST(s.originalEndTime AS time) = CAST(:originalEndTime AS time) or :originalEndTime is null) " +
		       "AND (s.originalRoom.roomId = :originalRoomId or :originalRoomId is null)")
		List<InvoiceEntity> findByOriginalShowtimeDetails(
		        @Param("originalShowDate") LocalDate originalShowDate,
		        @Param("originalStartTime") LocalTime originalStartTime,
		        @Param("originalEndTime") LocalTime originalEndTime,
		        @Param("originalRoomId") Integer originalRoomId);

	List<InvoiceEntity> findByAccount(AccountEntity account); // Tìm tất cả hóa đơn của tài khoản

}
