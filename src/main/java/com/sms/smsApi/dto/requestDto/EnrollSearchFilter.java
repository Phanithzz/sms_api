package com.sms.smsApi.dto.requestDto;

import com.sms.smsApi.model.enums.EnrollmentStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnrollSearchFilter {
        // search
        private String keyword;

        // filters
        private String studentId;

        private Integer homeroomClassId;

        private Integer academicYearId;
        private EnrollmentStatus status;
        // pagination
        private Integer page = 0;
        private Integer size = 10;

        // sorting
        private String sortBy = "id";
        private String direction = "asc";

}
