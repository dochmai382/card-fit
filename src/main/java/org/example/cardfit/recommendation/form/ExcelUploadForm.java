package org.example.cardfit.recommendation.form;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ExcelUploadForm(
        @NotNull(message = "파일을 선택해주세요")
        MultipartFile file
) {
}
