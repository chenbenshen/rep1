package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import com.itheima.service.CategoryService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐信息和套餐菜品信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto)
    {
        setmealService.saveWithSetmealDish(setmealDto);
        return R.success("添加成功");
    }
    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name)
    {
        Page<Setmeal> setmealPageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPageInfo = new Page<SetmealDto>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(name!=null,Setmeal::getName,name);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPageInfo,setmealLambdaQueryWrapper);
        BeanUtils.copyProperties(setmealPageInfo,setmealDtoPageInfo,"recodes");
        List<Setmeal> records = setmealPageInfo.getRecords();
        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Category category = categoryService.getById(item.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPageInfo.setRecords(collect);
        return R.success(setmealDtoPageInfo);
    }
    /**
     * 根据id来查询对应的信息
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id)
    {
        SetmealDto byIdWithSetmealDish = setmealService.getByIdWithSetmealDish(id);
        return R.success(byIdWithSetmealDish);
    }
    /**
     * 修改套餐信息
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto)
    {
        setmealService.updateWithSetmealDish(setmealDto);
        return R.success("修改成功");
    }
    /**
     * 指定id删除套餐（包括批量删除）
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids)
    {
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }
    /**
     * 停售或批量停售套餐
     */
    @PostMapping("/status/0")
    public R<String> stop(@RequestParam List<Long> ids)
    {
        setmealService.stop(ids);
        return R.success("停售成功");
    }
    /**
     * 套餐集合
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal)//不用加@RequestBody,因为不是Json模式，是key->value
    {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);
        return R.success(list);
    }
}
