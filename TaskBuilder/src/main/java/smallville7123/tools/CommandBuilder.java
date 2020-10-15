package smallville7123.tools;

import java.util.ArrayList;

abstract class CommandBuilder {
    public String TAG = "CommandBuilder";

    class Command {
        int command;
        Object[] arguments;

        Command(Command cmd) {
            command = cmd.command;
            arguments = cmd.arguments.clone();
        }

        Command(int cmd) {
            command = cmd;
            arguments = null;
        }

        Command(int cmd, Object args) {
            command = cmd;
            arguments = new Object[]{args};
        }

        Command(int cmd, Object ... args) {
            command = cmd;
            arguments = args;
        }
    };


    ArrayList<Command> commands = new ArrayList();

    protected void add(Command command) {
        commands.add(new Command(command));
    }

    public void add(int command_id) {
        commands.add(new Command(command_id));
    }

    public void add(int command_id, Object Argument) {
        commands.add(new Command(command_id, Argument));
    }

    public void add(int command_id, Object ... Arguments) {
        commands.add(new Command(command_id, Arguments));
    }

    public abstract void execute();
}
