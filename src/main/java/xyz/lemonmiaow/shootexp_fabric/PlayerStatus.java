package xyz.lemonmiaow.shootexp_fabric;

import xyz.lemonmiaow.shootexp_fabric.config.ModConfig;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class PlayerStatus {
    private int timesOfShoot = 0;
    private int stock;
    private boolean restoreShootTaskRunning = false;
    private boolean restoreStockTaskRunning = false;
    private long lastShootRestoreTick = -1;
    private long lastStockRestoreTick = -1;

    public PlayerStatus() {
        this.stock = ModConfig.getMaxStock();
    }

    public int getTimesOfShoot() {
        return timesOfShoot;
    }

    public void setTimesOfShoot(int times) {
        this.timesOfShoot = times;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getRequiredAttackTimes() {
        try {
            Expression e = new ExpressionBuilder(ModConfig.getRequiredAttackTimes())
                    .variables("SHOOT", "STOCK", "MAXSTOCK")
                    .build()
                    .setVariable("SHOOT", timesOfShoot)
                    .setVariable("STOCK", stock)
                    .setVariable("MAXSTOCK", ModConfig.getMaxStock());
            return (int) e.evaluate();
        } catch (Exception ex) {
            ShootexpFabric.LOGGER.warn("Failed to evaluate required-attack-times expression", ex);
            return 10;
        }
    }

    public int getShootAmount() {
        try {
            Expression e = new ExpressionBuilder(ModConfig.getShootAmount())
                    .variables("SHOOT", "STOCK", "MAXSTOCK")
                    .build()
                    .setVariable("SHOOT", timesOfShoot)
                    .setVariable("STOCK", stock)
                    .setVariable("MAXSTOCK", ModConfig.getMaxStock());
            return (int) e.evaluate();
        } catch (Exception ex) {
            ShootexpFabric.LOGGER.warn("Failed to evaluate shoot-amount expression", ex);
            return stock / 2;
        }
    }

    public int ejaculation() {
        int amount = 0;
        if (stock > 0) {
            amount = getShootAmount();
            if (amount > stock) {
                amount = stock;
            }
        }
        stock -= amount;
        timesOfShoot++;
        
        if (!restoreShootTaskRunning) {
            restoreShootTaskRunning = true;
            lastShootRestoreTick = -1;
        }
        if (!restoreStockTaskRunning) {
            restoreStockTaskRunning = true;
            lastStockRestoreTick = -1;
        }
        
        return amount;
    }

    public void tick(long currentTick) {
        if (restoreShootTaskRunning) {
            if (lastShootRestoreTick < 0) {
                lastShootRestoreTick = currentTick;
            } else if (currentTick - lastShootRestoreTick >= ModConfig.getRestoreShootPeriod()) {
                timesOfShoot -= ModConfig.getRestoreShootAmount();
                if (timesOfShoot < 0) {
                    timesOfShoot = 0;
                }
                lastShootRestoreTick = currentTick;
                
                if (timesOfShoot <= 0) {
                    restoreShootTaskRunning = false;
                }
            }
        }

        int maxStock = ModConfig.getMaxStock();
        if (restoreStockTaskRunning) {
            if (lastStockRestoreTick < 0) {
                lastStockRestoreTick = currentTick;
            } else if (currentTick - lastStockRestoreTick >= ModConfig.getRestoreStockPeriod()) {
                stock += ModConfig.getRestoreStockAmount();
                if (stock > maxStock) {
                    stock = maxStock;
                }
                lastStockRestoreTick = currentTick;
                
                if (stock >= maxStock) {
                    restoreStockTaskRunning = false;
                }
            }
        }
    }

    public void restoreShoot(int times) {
        timesOfShoot -= times;
        if (timesOfShoot < 0) {
            timesOfShoot = 0;
        }
    }

    public void restoreShootFull() {
        timesOfShoot = 0;
        restoreShootTaskRunning = false;
    }

    public void restoreStock(int amount) {
        stock += amount;
    }

    public void restoreStockFull() {
        stock = ModConfig.getMaxStock();
        restoreStockTaskRunning = false;
    }
}
