package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageCodesResolverTest {

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");

        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
            /**
             * messageCode = required.item
             * messageCode = required
             */
        }
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);

        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
            /**
             * messageCode = required.item.itemName
             * messageCode = required.itemName
             * messageCode = required.java.lang.String
             * messageCode = required
             */
        }
        // bindingResult.rejectValue("itemName", "required");

        assertThat(messageCodes).containsExactly("required.item.itemName", "required.itemName", "required.java.lang.String", "required");
        
    }
}
