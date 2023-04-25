package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.DishFlavor;
import org.example.mapper.DishFlavorMapper;
import org.example.service.DishFlavourService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavourServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavourService {
}
