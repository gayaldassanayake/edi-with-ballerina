import io.ballerina.projects.JBallerinaBackend;
import io.ballerina.projects.JvmTarget;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.Project;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.runtime.api.Module;
import io.ballerina.runtime.api.Runtime;
import io.ballerina.runtime.api.async.Callback;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.runtime.api.values.BError;
import io.ballerina.runtime.api.values.BString;

import java.nio.file.Path;
import java.util.Arrays;

public class Runner {
    public static void main(String[] args) {
        Module module = new Module("ballerina", "editools", "2");
        System.out.println(Arrays.toString(args));
        BString[] argsArray = new BString[args.length];
        for (int i = 0; i < args.length; i++) {
            argsArray[i] = StringUtils.fromString(args[i]);
        }
        Runtime balRuntime = Runtime.from(module);
        balRuntime.init();
        balRuntime.start();
        balRuntime.invokeMethodAsync("main", // decimal, decimal
                new Callback() {
                    @Override
                    public void notifySuccess(Object result) {
                    }

                    @Override
                    public void notifyFailure(BError error) {
                        System.out.println("Error: " + error);
                    }
                }, ValueCreator.createArrayValue(argsArray));
    }

    public static void generate(Path path) {
        // Load the project
        System.out.println(path);
        Project project = BuildProject.load(path);

        // Compile the project
        PackageCompilation compilation = project.currentPackage().getCompilation();

        // Generate the bytecode
        JBallerinaBackend jBallerinaBackend = JBallerinaBackend.from(compilation, JvmTarget.JAVA_17);
        jBallerinaBackend.emit(JBallerinaBackend.OutputType.EXEC, Path.of("resources/src/main/resources/baltool.jar"));
    }
}
