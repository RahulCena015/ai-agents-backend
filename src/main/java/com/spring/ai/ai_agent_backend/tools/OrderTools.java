package com.spring.ai.ai_agent_backend.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderTools {

    private static final Map<String, String> ORDER_STATUS = Map.of(
            "1042", "Shipped - arriving tomorrow",
            "1043", "Processing - not yet shipped",
            "1044", "cancelled - out of stock",
            "1045", "shipped - arriving next week"
    );

    @Tool(description = "Get the status of an customer order by its order Id")
    public static String getOrderStatus(String orderId) {
        return ORDER_STATUS.getOrDefault(orderId, "Order not found for given order id");
    }

    @Tool(description = "Cancel an order by its order Id")
    public static String cancelOrder(String orderId) {
        if (ORDER_STATUS.containsKey(orderId)) {
            return "Order " + orderId + " has been successfully cancelled.";
        } else {
            return "Order " + orderId + " not found.";
        }
    }
}
