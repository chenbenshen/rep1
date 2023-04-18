package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Category;
import com.itheima.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize)
    {
        log.info("category的分页查询");
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Category::getSort);
        categoryService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Category category)
    {
        //公共字段自动填充，这可以减少重复代码量
        //在实体类中实现
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户的id
//        Long employeeId = (Long) request.getSession().getAttribute("employeeId");
//        category.setCreateUser(employeeId);
//        category.setUpdateUser(employeeId);
        categoryService.save(category);
        return R.success("添加成功！");
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Category category)
    {
//        Long employeeId = (Long) request.getSession().getAttribute("employeeId");
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(employeeId);
        categoryService.updateById(category);
        return R.success("修改成功！");
    }
    @DeleteMapping
    public R<String> delete(Long ids)
    {
        categoryService.remove(ids);
        return R.success("删除成功！");
    }
    /**
     * 根据条件查询分类数据
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category)
    {
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);


    }
}
