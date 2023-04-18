package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.DishDto;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
   public  void saveWithSetmealDish(SetmealDto setmealDto);

   public SetmealDto getByIdWithSetmealDish(Long id);

   public void updateWithSetmealDish(SetmealDto setmealDto);

   public void removeWithDish(List<Long> ids);

   public void stop(List<Long> ids);
}
