package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseContext;
import org.example.common.R;
import org.example.entity.ShoppingCart;
import org.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("shoppingCart")
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        //获取 userId ,添加到 shoppingCart 中
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);

        //保存前需要判断菜品或套餐是否存在，如果存在得话数量加一
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if (shoppingCart.getDishId() != null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one != null){
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
            return R.success("添加成功");
        }

        shoppingCartService.save(shoppingCart);
        return R.success("添加成功");
    }


    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        //获取 userId
        Long userId = BaseContext.getCurrent();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if (dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        int number = one.getNumber() - 1;
        one.setNumber(number);
        shoppingCartService.update(one,queryWrapper);
        if (number <= 0){
            shoppingCartService.remove(queryWrapper);
        }

        return R.success("删除成功");
    }

    /**
     * 获取购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = BaseContext.getCurrent();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCarts);
    }

    /**
     * 清空购物车功能
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //获取 userId
        Long userId = BaseContext.getCurrent();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }
}
