package com.sitewhere.grpc.model;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.5.0)",
    comments = "Source: tenant-management.proto")
public final class TenantManagementGrpc {

  private TenantManagementGrpc() {}

  public static final String SERVICE_NAME = "com.sitewhere.grpc.model.TenantManagement";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest,
      com.sitewhere.grpc.model.TenantModel.GTenant> METHOD_CREATE_TENANT =
      io.grpc.MethodDescriptor.<com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest, com.sitewhere.grpc.model.TenantModel.GTenant>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.sitewhere.grpc.model.TenantManagement", "CreateTenant"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.sitewhere.grpc.model.TenantModel.GTenant.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TenantManagementStub newStub(io.grpc.Channel channel) {
    return new TenantManagementStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TenantManagementBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TenantManagementBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TenantManagementFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TenantManagementFutureStub(channel);
  }

  /**
   */
  public static abstract class TenantManagementImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Create a new tenant.
     * </pre>
     */
    public void createTenant(com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest request,
        io.grpc.stub.StreamObserver<com.sitewhere.grpc.model.TenantModel.GTenant> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CREATE_TENANT, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_CREATE_TENANT,
            asyncUnaryCall(
              new MethodHandlers<
                com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest,
                com.sitewhere.grpc.model.TenantModel.GTenant>(
                  this, METHODID_CREATE_TENANT)))
          .build();
    }
  }

  /**
   */
  public static final class TenantManagementStub extends io.grpc.stub.AbstractStub<TenantManagementStub> {
    private TenantManagementStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TenantManagementStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TenantManagementStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TenantManagementStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new tenant.
     * </pre>
     */
    public void createTenant(com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest request,
        io.grpc.stub.StreamObserver<com.sitewhere.grpc.model.TenantModel.GTenant> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_TENANT, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TenantManagementBlockingStub extends io.grpc.stub.AbstractStub<TenantManagementBlockingStub> {
    private TenantManagementBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TenantManagementBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TenantManagementBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TenantManagementBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new tenant.
     * </pre>
     */
    public com.sitewhere.grpc.model.TenantModel.GTenant createTenant(com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CREATE_TENANT, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TenantManagementFutureStub extends io.grpc.stub.AbstractStub<TenantManagementFutureStub> {
    private TenantManagementFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TenantManagementFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TenantManagementFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TenantManagementFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new tenant.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.sitewhere.grpc.model.TenantModel.GTenant> createTenant(
        com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_TENANT, getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_TENANT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TenantManagementImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TenantManagementImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_TENANT:
          serviceImpl.createTenant((com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest) request,
              (io.grpc.stub.StreamObserver<com.sitewhere.grpc.model.TenantModel.GTenant>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class TenantManagementDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.sitewhere.grpc.model.TenantServices.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TenantManagementGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TenantManagementDescriptorSupplier())
              .addMethod(METHOD_CREATE_TENANT)
              .build();
        }
      }
    }
    return result;
  }
}
