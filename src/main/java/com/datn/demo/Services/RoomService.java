package com.datn.demo.Services;

import com.datn.demo.DTO.RoomDTO;
import com.datn.demo.Entities.RoomEntity;
import com.datn.demo.Repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {

	  @Autowired
	    private RoomRepository roomRepository; // Tiêm repository

	    // Lấy tất cả phòng
	    public List<RoomDTO> getAllRooms() {
	        List<RoomEntity> rooms = roomRepository.findAll();
	        return rooms.stream().map(room -> new RoomDTO(room.getRoomId(), room.getRoomName())).collect(Collectors.toList());
	    }

	    // Lấy phòng theo ID
	    public Optional<RoomEntity> getRoomById(int roomId) {
	        return roomRepository.findById(roomId); // Phương thức đã có sẵn từ JpaRepository
	    }

	    // Tạo phòng mới
	    public RoomDTO createRoom(RoomDTO roomDTO) {
	        RoomEntity roomEntity = new RoomEntity();
	        roomEntity.setRoomName(roomDTO.getRoomName());
	        roomEntity = roomRepository.save(roomEntity);
	        return new RoomDTO(roomEntity.getRoomId(), roomEntity.getRoomName());
	    }

	    // Cập nhật phòng
	    public RoomDTO updateRoom(int roomId, RoomDTO roomDTO) {
	        Optional<RoomEntity> existingRoom = roomRepository.findById(roomId);
	        if (existingRoom.isPresent()) {
	            RoomEntity roomEntity = existingRoom.get();
	            roomEntity.setRoomName(roomDTO.getRoomName());
	            roomEntity = roomRepository.save(roomEntity);
	            return new RoomDTO(roomEntity.getRoomId(), roomEntity.getRoomName());
	        }
	        return null; // Hoặc xử lý khi không tìm thấy phòng
	    }

	    // Xóa phòng
	    public void deleteRoom(int roomId) {
	        roomRepository.deleteById(roomId);
	    }
}
