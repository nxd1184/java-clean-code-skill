package examples.before;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class SmellyOrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private RestTemplate http;

    public Order placeOrder(Order order, boolean notify, boolean retry) {
        if (order != null) {
            if (order.getCustomer() != null) {
                if (order.getCustomer().getAddress() != null) {
                    String zip = order.getCustomer().getAddress().getZipCode();
                    if (zip != null && zip.length() == 5) {
                        if (order.getTotal() > 10000) {
                            System.out.println("big order: " + order.getId());
                        }
                        try {
                            Order saved = orderRepo.save(order);
                            String url = "https://payments.example.com/charge?amount="
                                    + order.getTotal();
                            Map resp = http.postForObject(url, null, Map.class);
                            if (resp != null && "OK".equals(resp.get("status"))) {
                                if (notify) {
                                    http.postForObject(
                                        "https://email.example.com/send?to="
                                            + order.getCustomer().getEmail(),
                                        null, Map.class);
                                }
                                return saved;
                            } else {
                                return null;
                            }
                        } catch (Exception e) {
                            if (retry) {
                                return placeOrder(order, notify, false);
                            }
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }
}
