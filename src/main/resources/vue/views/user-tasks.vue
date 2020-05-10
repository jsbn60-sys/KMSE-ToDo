<template id="user-tasks">
    <app-frame>
        <div class="row d-flex justify-content-center container">
            <div class="col-md-8">
                <div class="card-hover-shadow-2x mb-3 card">
                    <div class="card-header-tab card-header">
                        <div class="card-header-title font-size-lg text-capitalize font-weight-normal"><i class="fa fa-tasks"></i>&nbsp;Task Lists</div>
                    </div>
                    <div class="scroll-area-sm">
                        <perfect-scrollbar class="ps-show-limits">
                            <div style="position: static;" class="ps ps--active-y">
                                <div class="ps-content">
                                    <ul class=" list-group list-group-flush">
                                        <li v-for="task in tasks" class="list-group-item">
                                            <div class="todo-indicator bg-warning"></div>
                                            <div class="widget-content p-0">
                                                <div class="widget-content-wrapper">
                                                    <div class="widget-content-left mr-2">
                                                        <div class="custom-checkbox custom-control"> <input class="custom-control-input" id="exampleCustomCheckbox12" type="checkbox"><label class="custom-control-label" for="exampleCustomCheckbox12">&nbsp;</label> </div>
                                                    </div>
                                                    <div class="widget-content-left">
                                                        <div class="widget-heading">{{task.title}} <div class="badge badge-danger ml-2">Rejected</div>
                                                        </div>
                                                        <div class="widget-subheading"><i>by {{username}}</i></div>
                                                    </div>
                                                    <div class="widget-content-right"> <button class="border-0 btn-transition btn btn-outline-success"> <i class="fa fa-check"></i></button> <button class="border-0 btn-transition btn btn-outline-danger"> <i class="fa fa-trash"></i> </button> </div>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </perfect-scrollbar>
                    </div>
                    <div class="d-block text-right card-footer"><button class="mr-2 btn btn-link btn-sm">Cancel</button><button class="btn btn-primary">Add Task</button></div>
                </div>
            </div>
        </div>


    </app-frame>
</template>
<script>
    Vue.component("user-tasks", {
        template: "#user-tasks",
        data: () => ({
            username: localStorage.getItem("username"),
            tasks: [],
        }),
        created() {
            const token = localStorage.getItem("token");
            fetch("/api/me/tasks?token=" + token, {
                method: "GET",
            })
                .then( async res => {
                    if(res.status === 200) {
                        let resdata = await res.json();
                        console.log(resdata);
                        this.tasks = resdata;
                        console.log(this.tasks)
                    }
                })
                .catch(() => alert("Error while fetching tasks"));
        }
    });
</script>
