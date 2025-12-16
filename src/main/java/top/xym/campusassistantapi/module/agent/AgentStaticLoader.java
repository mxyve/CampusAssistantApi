package top.xym.campusassistantapi.module.agent;

import com.alibaba.cloud.ai.agent.studio.loader.AgentLoader;
import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.agent.BaseAgent;
import org.springframework.stereotype.Component;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 静态代理加载器
 * 用于以编程方式提供代理的静态代理加载器。
 * 此加载器接收预先创建的代理实例的静态列表，并通过AgentLoader接口使其可用。
 * 非常适合已经拥有代理实例，只需要一种便捷的方式将它们包装到 AgentLoader 中的情况。
 *
 * @author moqi
 */
@Component
class AgentStaticLoader implements AgentLoader {

    private final Map<String, BaseAgent> agents = new ConcurrentHashMap<>();

    public AgentStaticLoader(BaseAgent agent) {
        GraphRepresentation representation = agent.getAndCompileGraph().stateGraph.getGraph(GraphRepresentation.Type.PLANTUML);
        System.out.println(representation.content());

        // 注册两个名称以保证兼容性
        // "research_agent" 是前端UI期望的默认名称
        this.agents.put("research_agent", agent);
        // 同时注册自定义名称
//        this.agents.put("smart_campus_assistant", agent);
        this.agents.put("campus_assistant_api", agent);
    }

    @Override
    @Nonnull
    public List<String> listAgents() {
        return agents.keySet().stream().toList();
    }

    @Override
    public BaseAgent loadAgent(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("代理名称不能为空或空白");
        }

        BaseAgent agent = agents.get(name);
        if (agent == null) {
            throw new NoSuchElementException("未找到代理：" + name);
        }

        return agent;
    }
}