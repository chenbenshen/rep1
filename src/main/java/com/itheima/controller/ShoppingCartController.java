package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.entity.ShoppingCart;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import com.itheima.service.SetmealService;
import com.itheima.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session)
    {
        //设置用户id，指定当前是哪个用户的购物车数据
        Long userId = (Long)session.getAttribute("userId");
        shoppingCart.setUserId(userId);
            //或者用线程的id
//        Long currentId = BaseContext.getCurrentId();
        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        if(dishId!=null)
        {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
            //添加到购物车的是菜品

        }
        else
        {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            //添加的是套餐
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        //如果已经存在，就在原来的数量基础上加1
        if(cartServiceOne!=null)
        {
//            Integer number = cartServiceOne.getNumber();
//            cartServiceOne.setNumber(number+1);
//            shoppingCartService.updateById(cartServiceOne);
            Integer number = cartServiceOne.getNumber();
            LambdaUpdateWrapper<ShoppingCart> shoppingCartLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getId,cartServiceOne.getId());
            shoppingCartLambdaUpdateWrapper.set(ShoppingCart::getNumber,number+1);
            shoppingCartService.update(shoppingCartLambdaUpdateWrapper);
            cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        }
        else {
            //如果不存在，则添加到购物车，数量默认是1
            //数据库的Shopping表的number字段已经默认是1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne=shoppingCart;//这个是为空的，添加完之后要赋值覆盖；
        }
        return R.success(cartServiceOne);
    }
    /**
     * 查看购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list()
    {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartLambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(list);
    }
    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean()
    {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("清空成功");
    }
}
