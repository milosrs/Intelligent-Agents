import { Aid } from "./aid";

export class AclMessage{

    public performative : string;
    public sender : Aid;
    public receivers : Aid[];
    public replyTo : Aid;
    public content : string;
    public language : string;
    public encoding : string;
    public ontology : string;
    public protocol : string;
    public conversationId : string;
    public replyWith : string;
    public inReplyTo : string;
    public replyBy : string;

    constructor(){
        this.performative = "";
        this.receivers = [];
        this.content = "";
        this.language = "";
        this.encoding = "";
        this.ontology = "";
        this.protocol = "";
        this.conversationId = "";
        this.replyWith = "";
        this.inReplyTo = "";
        this.replyBy = "";
    }


}