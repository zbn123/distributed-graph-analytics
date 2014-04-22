package com.soteradefense.dga.weaklyconnectedcomponents;

import org.apache.giraph.GiraphRunner;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;


public class WeaklyConnectedComponents extends BasicComputation<Text, Text, NullWritable, Text> {

    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new GiraphRunner(), args));
    }

    @Override
    public void compute(Vertex<Text, Text, NullWritable> vertex, Iterable<Text> messages) throws IOException {
        try {
            if (getSuperstep() == 0) {
                broadcastGreatestNeighbor(vertex);
                return;
            }
            boolean changed = false;
            String maxId = vertex.getValue().toString();
            for (Text incomingMessage : messages) {
                if (maxId.compareTo(incomingMessage.toString()) < 0) {
                    maxId = incomingMessage.toString();
                    changed = true;
                }
            }
            broadcastUpdates(vertex, changed, maxId);
        } catch (Exception e) {
            System.err.print(e.toString());
        }
    }

    private void broadcastGreatestNeighbor(Vertex<Text, Text, NullWritable> vertex) {
        String maxId = vertex.getId().toString();
        for (Edge<Text, NullWritable> edge : vertex.getEdges()) {
            if(maxId.compareTo(edge.getTargetVertexId().toString()) < 0){
                maxId = edge.getTargetVertexId().toString();
            }
        }
        broadcastUpdates(vertex, true, maxId);
    }

    private void broadcastUpdates(Vertex<Text, Text, NullWritable> vertex, boolean changed, String maxId) {
        if (changed) {
            vertex.setValue(new Text(maxId));
            sendMessageToAllEdges(vertex, new Text(vertex.getValue().toString()));
        }
        vertex.voteToHalt();
    }
}
