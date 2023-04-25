package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.DishDto;
import org.example.entity.Dish;

public interface DishService extends IService<Dish> {
    public void saveWithDishFlavour(DishDto dishDto);

    public DishDto getByIdWithFlavour(Long id);

    void updateWithDishFlavour(DishDto dishDto);
}
