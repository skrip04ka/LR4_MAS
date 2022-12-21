package org.example;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DfHelper {

    /**
     * register an agent in DF Agent with specific service.
     * @param a - agent
     * @param serviceName - service name
     * @return if registration was successful
     */
    public static boolean registerAgent(Agent a, String serviceName){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(a.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceName);
        sd.setName(a.getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(a, dfd);
            return true;
        } catch (FIPAException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * find all agents in DF registered with given service name
     * @param a - agent
     * @param serviceName - service name
     * @return collection of found agents
     */
    public static List<AID> findAgents(Agent a, String serviceName){
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceName);
        dfd.addServices(sd);
        try {
            DFAgentDescription[] search = DFService.search(a, dfd);
            return Arrays.stream(search)
                    .map(DFAgentDescription::getName)
                    .collect(Collectors.toList());

        } catch (FIPAException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
