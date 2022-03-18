package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        /**
         * validation
         * model에 오류를 담아 클라이언트로 반환
         */

        // 1. 검증 오류 결과를 보관
        /**
         * bindingResult 는 자동으로 view에 넘어가므로 model에 담지 않아도 된다.
         * bindingResult 객체를 파라미터로 받아서 에러를 처리한다.
         */

        /**
         * bindingResult.addError(new FieldError("객체명", "필드명", "클라이언트 에러 메세지");
         * 글로벌 오류가 아닌 일반 필드 에러는 new FieldError()에 담을 수 있다.
         */
        // 2. 검증 로직
        if (!StringUtils.hasText(item.getItemName())) { // item 의 name 이 null 이라면
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용됩니다."));
        }

        // 3. 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();

            /**
             * bindingResult.addError(new ObjectError("객체명", "클라이언트 에러 메세지");
             * 글로벌 오류는 new ObjectError() 에 담아 에러를 처리할 수 이다.
             */
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 *  수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 4. 검증에 실패하면 다시 입력 폼으로
        /**
         * bindingResult.hasErrors();
         * 에러 유무를 판단한다.
         */
        if (bindingResult.hasErrors()) {
            log.info("bindingResult={}", bindingResult);
            /**
             * bindingResult 는 자동으로 view에 넘어가므로 model에 담지 않아도 된다.
             */
            return "validation/v2/addForm";
        }

        // 검증 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

