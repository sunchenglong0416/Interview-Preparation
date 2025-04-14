# 基于redis实现订单延时取消功能


# 场景1-订单支付超时取消

## 订单创建时间+延时最大时间 组成最晚支付时间 
redisTemplate.opsForZSet().add("A_order_cancel", orderId, "订单创建时间"+30分钟);

## 定时任务扫描，与系统当前时间比较，订单是否超时

