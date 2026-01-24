package org.example.cardfit.recommendation.form;

import org.springframework.web.multipart.MultipartFile;

public record ExcelUploadForm(
        MultipartFile file
) {
}
