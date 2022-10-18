package gukjin.jpa.controller;

import gukjin.jpa.domain.Member;
import gukjin.jpa.domain.Order;
import gukjin.jpa.domain.item.Item;
import gukjin.jpa.repository.OrderSearch;
import gukjin.jpa.service.ItemService;
import gukjin.jpa.service.MemberService;
import gukjin.jpa.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class OrderController {

    @Autowired private final OrderService orderService;
    @Autowired private final MemberService memberService;
    @Autowired private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();
        model.addAttribute("members", members);
        model.addAttribute("items", items);
        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {

        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancleOrder(@PathVariable("orderId") Long orderId ){
        orderService.orderCancel(orderId);
        return "redirect:/orders";
    }
}
