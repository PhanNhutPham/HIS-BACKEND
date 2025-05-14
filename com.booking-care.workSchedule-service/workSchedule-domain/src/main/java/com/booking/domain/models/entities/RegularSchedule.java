package com.booking.domain.models.entities;

import com.booking.domain.models.enums.DayOfWeek;
import com.booking.domain.models.enums.DayOfWeekKeyDeserializer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "regular_Schedule")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegularSchedule {
    @Id
    private String rSchedule_id;

    @PrePersist
    public void generateId() {
        this.rSchedule_id = "rSchedule" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    @ElementCollection
    @CollectionTable(name = "schedule_day_of_week", joinColumns = @JoinColumn(name = "rSchedule_id"))
    @MapKeyEnumerated(EnumType.STRING)  // Sử dụng Enum làm key
    @Column(name = "is_working")
    @JsonDeserialize(keyUsing = DayOfWeekKeyDeserializer.class)
    private Map<DayOfWeek, Boolean> dayOfTheWeek = new EnumMap<>(DayOfWeek.class); // Khởi tạo sẵn EnumMap

    private LocalDate currentDay;

    @OneToOne(mappedBy = "regularSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    private WorkSchedule workSchedule;

    // Setter mới để đảm bảo map dayOfTheWeek được khởi tạo đúng cách
    public void setDayOfTheWeek(Map<String, Boolean> dayOfTheWeek) {
        if (this.dayOfTheWeek == null) {
            this.dayOfTheWeek = new EnumMap<>(DayOfWeek.class); // Khởi tạo nếu null
        }
        // Duyệt qua từng mục trong map và chuyển từ String thành Enum DayOfWeek
        for (Map.Entry<String, Boolean> entry : dayOfTheWeek.entrySet()) {
            try {
                DayOfWeek day = DayOfWeek.valueOf(entry.getKey().toUpperCase()); // Chuyển từ String thành Enum
                this.dayOfTheWeek.put(day, entry.getValue()); // Gán giá trị vào Map
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid day of the week: " + entry.getKey());
            }
        }
    }

}
