package com.driverService.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDetailsDTO {
    @NotBlank(message = "Enter the bank holder Name")
    private String bankHolderName;

    @NotBlank(message = "Select the bank")
    private String bankName;


    @Pattern(regexp = "^[0-9]{9,18}$", message = "Invalid account number. It should be 9 to 18 digits long.")
    private String accountNumber;

    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code. Format should be like: SBIN0001234")
    private String ifscCode;
}

