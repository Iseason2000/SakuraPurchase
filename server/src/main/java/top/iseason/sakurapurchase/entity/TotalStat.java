package top.iseason.sakurapurchase.entity;

import lombok.Data;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TotalStat {
    final private static DecimalFormat fmt = new DecimalFormat("#.##");
    private List<Stat> stats;

    public TotalStat(List<Stat> stats) {
        this.stats = stats;
    }

    public String getTotalAmount() {
        return fmt.format(stats.stream().mapToDouble(Stat::getAmount).sum());
    }

    public String getTotalCount() {
        return fmt.format(stats.stream().mapToInt(Stat::getCount).sum());
    }

    public List<Double> getAmount() {
        return stats.stream().map(it -> (double) Math.round(it.getAmount() * 100) / 100).collect(Collectors.toList());
    }

    public List<String> getPeriod() {
        return stats.stream().map(Stat::getPeriod).collect(Collectors.toList());
    }

}
