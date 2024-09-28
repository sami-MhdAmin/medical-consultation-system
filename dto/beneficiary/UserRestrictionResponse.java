package com.grad.akemha.dto.beneficiary;

import com.grad.akemha.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRestrictionResponse {
    private Long id;
    private boolean isActive ;
    public UserRestrictionResponse(User user) {
        this.id = user.getId();
        this.isActive = user.getIsActive();
    }
}
