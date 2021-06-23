package ai.kaze.ServerChan.CommandDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Logger;

public abstract class CommandDispatcher {
    private final Logger logger = Logger.getLogger("Minecraft");

    public void dispatchCommand(String sender, String command, Object... parameters) {
        Method[] methods = this.getClass().getMethods();
        boolean isOp = this.isOp(sender);
        Arrays.stream(methods).filter(method -> {
            if (method.isAnnotationPresent(CommandHandler.class)) {
                CommandHandler cmd = method.getAnnotation(CommandHandler.class);
                if (cmd.command().equals(command)) {
                    if (cmd.op()) {
                        return isOp;
                    } else {
                        return true;
                    }
                }
            }
            return false;
        }).forEach(method -> {
            try {
                method.invoke(this, sender, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.severe(e.getMessage());
            }
        });
    }

    abstract boolean isOp(String user);
}
