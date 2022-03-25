package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {

    /**
     * 검증하는 클래스를 지원하는지에 대한 여부 체크
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz); // isAssignableFrom는 아래 두 가지를 모두 체크할 수 있다.
        // item === clazz (클래스로 넘어오는 타입이 Item 타입과 같은지)
        // item === subItem (Item에 있는 자식 클래스도 검증할 수 있도록)
    }

    /**
     * 실제 검증 로직 작성
     * target 은 검증 대상
     * Errors 는 BindingResult의 부모
     */
    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target; // 검증 대상

        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required");
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증 (글로벌 오브젝트 오류)
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();

            if (resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

    }
}
