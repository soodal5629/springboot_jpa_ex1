package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional // readOnly = false로 해야 저장이 됨
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    /* 변경감지를 통해 update하는 방법 -> 실무에서는 이것을 사용하길 권장! */
    // 필요한 것만 update 가능하므로!
    @Transactional // 변경감지(dirty checking)를 하여 DB에 update 쿼리문이 자동으로 날아감
    public void updateItem(Long itemId, Book param){
        Item findItem = itemRepository.findOne(itemId); // 영속상태임
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
    }
    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long id){
        return itemRepository.findOne(id);
    }
}
