import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;

/**
 * 布隆过滤器测试程序
 * 演示布隆过滤器的基本工作原理
 */
public class BloomFilterTest {
    
    public static void main(String[] args) {
        // 1. 创建布隆过滤器
        BloomFilter<String> bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(Charset.defaultCharset()),
                1000,  // 预期插入1000个元素
                0.01   // 误判率1%
        );
        
        System.out.println("=== 布隆过滤器测试 ===");
        System.out.println("预期插入数量: 1000");
        System.out.println("误判率: 1%");
        System.out.println();
        
        // 2. 添加一些商品ID到布隆过滤器
        String[] existingItems = {"1001", "1002", "1003", "1004", "1005"};
        
        System.out.println("向布隆过滤器添加商品ID:");
        for (String itemId : existingItems) {
            bloomFilter.put(itemId);
            System.out.println("  添加商品ID: " + itemId);
        }
        System.out.println();
        
        // 3. 测试存在的商品ID
        System.out.println("测试已添加的商品ID:");
        for (String itemId : existingItems) {
            boolean exists = bloomFilter.mightContain(itemId);
            System.out.println("  商品ID " + itemId + ": " + (exists ? "可能存在" : "一定不存在"));
        }
        System.out.println();
        
        // 4. 测试不存在的商品ID
        String[] nonExistingItems = {"2001", "2002", "2003", "2004", "2005"};
        System.out.println("测试未添加的商品ID:");
        for (String itemId : nonExistingItems) {
            boolean exists = bloomFilter.mightContain(itemId);
            System.out.println("  商品ID " + itemId + ": " + (exists ? "可能存在(误判)" : "一定不存在"));
        }
        System.out.println();
        
        // 5. 显示统计信息
        System.out.println("=== 布隆过滤器统计信息 ===");
        System.out.println("预期误判率: " + String.format("%.4f", bloomFilter.expectedFpp()));
        System.out.println("近似元素数量: " + bloomFilter.approximateElementCount());
        
        System.out.println();
        System.out.println("=== 重要说明 ===");
        System.out.println("1. 布隆过滤器创建时是空的，需要手动添加数据");
        System.out.println("2. 返回false表示元素一定不存在");
        System.out.println("3. 返回true表示元素可能存在（有误判可能）");
        System.out.println("4. 通常用于第一层过滤，减少数据库查询");
    }
}